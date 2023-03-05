use super::super::request_guards::greeting_request_guard::GreetingAuth;
use super::super::AppState;

use crate::models::greeting_message::GreetingMessage;
use actix_web::{get, web, HttpResponse};

#[get("/api/greeting")]
async fn get_greeting_message(auth: GreetingAuth, data: web::Data<AppState>) -> HttpResponse {
    let users = data.users.lock().unwrap();
    for user in &(*users) {
        if user.login.eq(&auth.username) {
            return HttpResponse::Ok().json(GreetingMessage::from(user));
        }
    }
    HttpResponse::NotFound().body("There is no such a user".to_string())
}
