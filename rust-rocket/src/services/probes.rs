use rocket::get;

#[get("/start")]
pub fn get_startup() -> &'static str {
    "{\"ok\":true}"
}

#[get("/ready")]
pub fn get_readiness() -> &'static str {
    "{\"ok\":true}"
}

#[get("/live")]
pub fn get_liveness() -> &'static str {
    "{\"ok\":true}"
}
