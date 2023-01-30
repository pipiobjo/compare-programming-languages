use rocket::get;

// TODO: implement getting version

#[get("/v1/version")]
pub fn get_version() -> &'static str {
    "{\"version\":\"\"}"
}
