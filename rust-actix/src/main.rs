use actix_web::{web, App, HttpServer};
use futures::future;
use rustactix::services::greeting::get_greeting_message;
use rustactix::services::probes::{get_liveness, get_readiness, get_startup};
use rustactix::services::user::{create_user, get_users};
use rustactix::services::version::get_version;
use rustactix::AppState;

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    let app_state = web::Data::new(AppState::new());
    let server1 = HttpServer::new(move || {
        App::new()
            .app_data(app_state.clone())
            .service(get_greeting_message)
            .service(get_version)
            .service(create_user)
            .service(get_users)
    })
    .bind("0.0.0.0:8080")?
    .run();
    let server2 = HttpServer::new(|| {
        App::new()
            .service(get_liveness)
            .service(get_readiness)
            .service(get_startup)
    })
    .bind("0.0.0.0:8081")?
    .run();
    future::try_join(server1, server2).await?;
    Ok(())
}
