# REST API in Rust with Actix (optimized)
This version is the same version as rust-actix with just some properties to optimizing the size of released binary. 

## Libs
[Rust Book](https://doc.rust-lang.org/book/title-page.html)

[Actix](https://actix.rs/)


## [Optimizing dependencies](https://docs.rust-embedded.org/book/unsorted/speed-vs-size.html#optimizing-dependencies)
There's a Cargo feature named profile-overrides that lets you override the optimization level of dependencies. You can use that feature to optimize all dependencies for size while keeping the top crate unoptimized and debugger friendly.

You find the properties in Cargo.toml and here you find the meaning of them: https://github.com/johnthagen/min-sized-rust

```rust
[profile.release]
opt-level = 'z'     # Optimize for size
lto = true          # Enable link-time optimization
codegen-units = 1   # Reduce number of codegen units to increase optimizations
panic = 'abort'     # Abort on panic
strip = true        # Strip symbols from binary*
```

