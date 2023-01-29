use super::super::AppState;
use crate::models::new_user::NewUser;
use crate::models::ui_user::UiUser;
use crate::models::user::User;
use rocket::http::Status;
use rocket::serde::json::Json;
use rocket::{get, post, State};

#[post("/api/user", data = "<new_user>")]
pub async fn create_user(new_user: Json<NewUser>, app_state: &State<AppState>) -> (Status, String) {
    let mut users = app_state.users.lock().unwrap();
    if users.iter().any(|user| user.login.eq(&new_user.login)) {
        return (Status::BadRequest, "user already exists".to_string());
    }
    let new_id = app_state.next_id();
    users.push(User::from(new_id, &new_user));
    (Status::Ok, new_id.to_string())
}

#[get("/api/user?<login>")]
pub async fn get_users(
    login: Option<String>,
    app_state: &State<AppState>,
) -> (Status, Json<Vec<UiUser>>) {
    let users = app_state.users.lock().unwrap();
    if login.is_some() {
        return return_this_user(&login.as_ref().unwrap(), &(*users));
    }
    let mut ui_users: Vec<UiUser> = Vec::new();
    for user in &(*users) {
        ui_users.push(UiUser::from(user));
    }
    (Status::Ok, Json(ui_users))
}

fn return_this_user(username: &String, users: &Vec<User>) -> (Status, Json<Vec<UiUser>>) {
    for user in users {
        if user.login.eq(username) {
            return (Status::Ok, Json(vec![UiUser::from(user)]));
        }
    }
    (Status::NotFound, Json(vec![]))
}
