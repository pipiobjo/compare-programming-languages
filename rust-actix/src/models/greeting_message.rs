use super::user::User;
use serde::Serialize;

#[derive(Serialize)]
pub struct GreetingMessage {
    msg: String,
    firstname: String,
    lastname: String,
}

impl GreetingMessage {
    pub fn from(user: &User) -> GreetingMessage {
        GreetingMessage {
            firstname: String::from(&user.firstname),
            lastname: String::from(&user.lastname),
            msg: format!("Hello {} {}!", user.firstname, user.lastname),
        }
    }
}
