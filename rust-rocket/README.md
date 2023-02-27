# API in Rust with Rocket
Rocket provides primitives to build web servers and applications with Rust: Rocket provides routing, pre-processing of requests, and post-processing of responses; the rest is up to you. Your application code instructs Rocket on what to pre-process and post-process and fills the gaps between pre-processing and post-processing.

## Libs
[Rust Book](https://doc.rust-lang.org/book/title-page.html)

[Rocket](https://rocket.rs/v0.5-rc/guide/)
\
\
\
Rocket's design is centered around three core philosophies:

### 1. Security, correctness, and developer experience are paramount.

The path of least resistance should lead you to the most secure, correct web application, though security and correctness should not come at the cost of a degraded developer experience. Rocket is easy to use while taking great measures to ensure that your application is secure and correct without cognitive overhead.

### 2. All request handling information should be typed and self-contained.

Because the web and HTTP are themselves untyped (or stringly typed, as some call it), this means that something or someone has to convert strings to native types. Rocket does this for you with zero programming overhead. What's more, Rocket's request handling is self-contained with zero global state: handlers are regular functions with regular arguments.

### 3. Decisions should not be forced.

Templates, serialization, sessions, and just about everything else are all pluggable, optional components. While Rocket has official support and libraries for each of these, they are completely optional and swappable.

<br><br>


## Rocket vs Actix: my opinion

Both Rocket and Actix perform almost identically. Rocket seems slower maybe because it adds more headers and transfers more data.

In internet there is more resources and examples for Actix and I like their documentation more. 

Rocket has its own property file (Rocket.toml), what could be very useful.