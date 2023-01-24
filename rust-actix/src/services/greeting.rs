use super::super::AppState;
use crate::models::greeting_message::GreetingMessage;
use actix_web::{get, web, HttpResponse};
use actix_web_httpauth::extractors::basic::BasicAuth;

#[get("/api/greeting")]
pub async fn get_greeting_message(auth: BasicAuth, data: web::Data<AppState>) -> HttpResponse {
    let users = data.users.lock().unwrap();
    for user in &(*users) {
        if user.login.eq(auth.user_id()) && user.password.eq(auth.password().unwrap_or_default()) {
            return HttpResponse::Ok().json(GreetingMessage::from(user));
        }
    }
    HttpResponse::NotFound().body("There is no such a user".to_string())
}
