use actix_web::{get, HttpResponse};

// TODO: implement getting version

#[get("/v1/version")]
async fn get_version() -> HttpResponse {
    HttpResponse::Ok().body("{\"version\":\"\"}")
}
