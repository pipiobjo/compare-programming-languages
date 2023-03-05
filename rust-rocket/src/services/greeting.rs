use super::super::request_guards::greeting_request_guard::GreetingAuth;
use super::super::AppState;
use crate::models::greeting_message::GreetingMessage;
use rocket::serde::json::Json;
use rocket::{get, http::Status, State};

#[get("/api/greeting")]
pub fn get_greeting_message(
    auth: GreetingAuth,
    data: &State<AppState>,
) -> (Status, Option<Json<GreetingMessage>>) {
    let users = data.users.lock().unwrap();
    for user in &(*users) {
        if user.login.eq(&auth.username) {
            return (Status::Ok, Option::Some(Json(GreetingMessage::from(user))));
        }
    }
    (Status::NotFound, Option::None)
}
