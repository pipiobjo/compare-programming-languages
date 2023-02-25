use super::super::super::AppState;
use super::{GreetingAuth, GreetingAuthError};
use base64;
use rocket::http::Status;
use rocket::outcome::Outcome;
use rocket::request::{self, FromRequest, Request};

// https://github.com/Owez/rocket-basicauth

/// Decodes a base64-encoded string into a tuple of `(username, password)` or a
/// [Option::None] if badly formatted, e.g. if an error occurs
fn decode_to_creds<T: Into<String>>(base64_encoded: T) -> Option<(String, String)> {
    let decoded_creds = match base64::decode(base64_encoded.into()) {
        Ok(cred_bytes) => String::from_utf8(cred_bytes).unwrap(),
        Err(_) => return None,
    };

    if let Some((username, password)) = decoded_creds.split_once(":") {
        Some((username.to_owned(), password.to_owned()))
    } else {
        None
    }
}

impl GreetingAuth {
    /// Creates a new [BasicAuth] struct/request guard from a given plaintext
    /// http auth header or returns a [Option::None] if invalid
    pub fn new<T: Into<String>>(auth_header: T) -> Option<Self> {
        let key = auth_header.into();

        if key.len() < 7 || &key[..6] != "Basic " {
            return None;
        }

        let (username, password) = decode_to_creds(&key[6..])?;
        Some(Self { username, password })
    }
}

#[rocket::async_trait]
impl<'r> FromRequest<'r> for GreetingAuth {
    type Error = GreetingAuthError;

    async fn from_request(request: &'r Request<'_>) -> request::Outcome<Self, Self::Error> {
        let keys: Vec<_> = request.headers().get("Authorization").collect();
        match keys.len() {
            0 => Outcome::Forward(()),
            1 => match GreetingAuth::new(keys[0]) {
                Some(auth_header) => match request.rocket().state::<AppState>() {
                    Some(data) => {
                        let users = data.users.lock().unwrap();

                        for user in &(*users) {
                            if user.login.eq(&auth_header.username)
                                && user.password.eq(&auth_header.password)
                            {
                                return Outcome::Success(auth_header);
                            }
                        }
                        Outcome::Failure((Status::NotFound, GreetingAuthError::NotRegistered))
                    }
                    None => Outcome::Failure((Status::NotFound, GreetingAuthError::NotRegistered)),
                },
                None => Outcome::Failure((Status::BadRequest, GreetingAuthError::Invalid)),
            },
            _ => Outcome::Failure((Status::BadRequest, GreetingAuthError::BadCount)),
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn decode_to_creds_check() {
        // Tests: name:password
        assert_eq!(
            decode_to_creds("bmFtZTpwYXNzd29yZA=="),
            Some(("name".to_string(), "password".to_string()))
        );
        // Tests: name:pass:word
        assert_eq!(
            decode_to_creds("bmFtZTpwYXNzOndvcmQ="),
            Some(("name".to_string(), "pass:word".to_string()))
        );
        // Tests: emptypass:
        assert_eq!(
            decode_to_creds("ZW1wdHlwYXNzOg=="),
            Some(("emptypass".to_string(), "".to_string()))
        );
        // Tests: :
        assert_eq!(
            decode_to_creds("Og=="),
            Some(("".to_string(), "".to_string()))
        );
        assert_eq!(decode_to_creds("bm9jb2xvbg=="), None);
    }
}