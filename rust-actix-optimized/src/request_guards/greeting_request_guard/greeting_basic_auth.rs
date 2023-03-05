use super::super::super::AppState;
use super::GreetingAuth;
use actix_web::dev::Payload;
use actix_web::error::{ErrorNotFound, ErrorUnauthorized};
use actix_web::http::header::Header;
use actix_web::web::Data;
use actix_web::Error;
use actix_web::{FromRequest, HttpRequest};
use actix_web_httpauth::headers::authorization::{Authorization, Basic};
use futures::future::{err, ok, Ready};

impl FromRequest for GreetingAuth {
    type Error = Error;
    type Future = Ready<Result<GreetingAuth, Error>>;

    fn from_request(req: &HttpRequest, _pl: &mut Payload) -> Self::Future {
        match Authorization::<Basic>::parse(req) {
            Ok(auth) => {
                let data = req.app_data::<Data<AppState>>().unwrap().clone();
                let users = data.users.lock().unwrap();

                for user in &(*users) {
                    if user.login.eq(auth.as_ref().user_id())
                        && user
                            .password
                            .eq(auth.as_ref().password().unwrap_or_default())
                    {
                        return ok(GreetingAuth {
                            username: user.login.clone(),
                            _password: user.password.clone(),
                        });
                    }
                }
                err(ErrorNotFound("There is no such a user"))
            }
            Err(_) => err(ErrorUnauthorized("Basic authentication failed")),
        }
    }
}
