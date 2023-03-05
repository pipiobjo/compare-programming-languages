# REST API in Rust with Actix
An application developed with Actix Web will expose an HTTP server contained within a native executable. You can either put this behind another HTTP server like nginx or serve it up as-is. Even in the complete absence of another HTTP server Actix Web is powerful enough to provide HTTP/1 and HTTP/2 support as well as TLS (HTTPS). This makes it useful for building small services ready for production.

Most importantly: Actix Web runs on Rust 1.59 or later and it works with stable releases.
<br><br>

## Libs
[Rust Book](https://doc.rust-lang.org/book/title-page.html)

[Actix](https://actix.rs/)

<br>

## Rocket vs Actix: my opinion

Both Rocket and Actix perform almost identically. Rocket seems slower maybe because it adds more headers and transfers more data.

In internet there is more resources and examples for Actix and I like their documentation more. 

Rocket has its own property file (Rocket.toml), what could be very useful.