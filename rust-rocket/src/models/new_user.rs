use serde::Deserialize;

#[derive(Deserialize)]
pub struct NewUser {
    pub login: String,
    pub password: String,
    pub firstname: String,
    pub lastname: String,
}
