#[macro_use]
extern crate rocket;
use futures::future;
use rustrocket::services::greeting::get_greeting_message;
use rustrocket::services::probes::{get_liveness, get_readiness, get_startup};
use rustrocket::services::user::{create_user, get_users};
use rustrocket::services::version::get_version;
use rustrocket::AppState;

#[rocket::main]
async fn main() {
    let figment1 = rocket::Config::figment().merge(("port", 8080));
    let figment2 = rocket::Config::figment().merge(("port", 8081));

    let server1 = rocket::custom(figment1)
        .manage(AppState::new())
        .mount(
            "/",
            routes![get_version, create_user, get_users, get_greeting_message],
        )
        .launch();
    let server2 = rocket::custom(figment2)
        .mount("/ops", routes![get_liveness, get_readiness, get_startup])
        .launch();
    future::try_join(server1, server2).await.unwrap_err();
}
