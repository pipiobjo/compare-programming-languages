use std::sync::Mutex;
mod models;
mod request_guards;
pub mod services;
use models::user::User;

pub struct AppState {
    users: Mutex<Vec<User>>,
    last_id: Mutex<u64>,
}

impl AppState {
    pub fn new() -> AppState {
        AppState {
            users: Mutex::new(vec![]),
            last_id: Mutex::new(0),
        }
    }

    fn next_id(&self) -> u64 {
        *(self.last_id.lock().unwrap()) += 1;
        *(self.last_id.lock().unwrap())
    }
}

#[cfg(test)]
mod tests {
    // use super::*;
    // #[test]
}
