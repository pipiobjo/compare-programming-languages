use super::new_user::NewUser;

pub struct User {
    pub _id: u64,
    pub login: String,
    pub password: String,
    pub firstname: String,
    pub lastname: String,
}

impl User {
    pub fn from(new_id: u64, new_user: &NewUser) -> User {
        User {
            _id: new_id,
            login: String::from(&new_user.login),
            password: String::from(&new_user.password),
            firstname: String::from(&new_user.firstname),
            lastname: String::from(&new_user.lastname),
        }
    }
}
