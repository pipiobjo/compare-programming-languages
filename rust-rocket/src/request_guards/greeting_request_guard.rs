mod greeting_basic_auth;

pub struct GreetingAuth {
    pub username: String,
    password: String,
}

#[derive(Debug)]
pub enum GreetingAuthError {
    BadCount,
    Invalid,
    NotRegistered,
}
