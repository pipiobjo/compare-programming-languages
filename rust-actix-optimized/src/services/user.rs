use super::super::AppState;
use crate::models::new_user::NewUser;
use crate::models::ui_user::UiUser;
use crate::models::user::User;
use actix_web::{get, post, web, HttpResponse};
use serde::Deserialize;

#[derive(Deserialize)]
struct UserParams {
    login: Option<String>,
}

#[post("/api/user")]
async fn create_user(new_user: web::Json<NewUser>, data: web::Data<AppState>) -> HttpResponse {
    let mut users = data.users.lock().unwrap();
    if users.iter().any(|user| user.login.eq(&new_user.login)) {
        return HttpResponse::BadRequest().body("user already exists".to_string());
    }

    let new_id = data.next_id();
    users.push(User::from(new_id, &new_user.into_inner()));
    HttpResponse::Ok().json(new_id)
}

#[get("/api/user")]
async fn get_users(params: web::Query<UserParams>, data: web::Data<AppState>) -> HttpResponse {
    let users = data.users.lock().unwrap();
    if params.login.is_some() {
        return return_this_user(&params.login.as_ref().unwrap(), &(*users));
    }
    let mut ui_users: Vec<UiUser> = Vec::new();
    for user in &(*users) {
        ui_users.push(UiUser::from(user));
    }
    HttpResponse::Ok().json(&ui_users)
}

fn return_this_user(username: &String, users: &Vec<User>) -> HttpResponse {
    for user in users {
        if user.login.eq(username) {
            return HttpResponse::Ok().json(UiUser::from(user));
        }
    }
    HttpResponse::NotFound().body("There is no such a user".to_string())
}
