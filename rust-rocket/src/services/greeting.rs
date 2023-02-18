use super::super::AppState;
use crate::models::greeting_message::GreetingMessage;
use rocket::serde::json::Json;
use rocket::{get, http::Status, State};
use rocket_basicauth::BasicAuth;

#[get("/api/greeting")]
pub async fn get_greeting_message(
    auth: BasicAuth,
    data: &State<AppState>,
) -> (Status, Option<Json<GreetingMessage>>) {
    let users = data.users.lock().unwrap();
    for user in &(*users) {
        if user.login.eq(&auth.username) && user.password.eq(&auth.password) {
            return (Status::Ok, Option::Some(Json(GreetingMessage::from(user))));
        }
    }
    (Status::NotFound, Option::None)
}
