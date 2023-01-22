use super::user::User;
use serde::Serialize;

#[derive(Serialize)]
pub struct UiUser {
    login: String,
    firstname: String,
    lastname: String,
}

impl UiUser {
    pub fn from(user: &User) -> UiUser {
        UiUser {
            login: String::from(&user.login),
            firstname: String::from(&user.firstname),
            lastname: String::from(&user.lastname),
        }
    }
}
