package router

import (
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/cors"
	"github.com/go-playground/validator/v10"
	"golang-chi/api/requestInterceptor"
	"golang-chi/api/resources/greeting"
	"golang-chi/api/resources/health"
	"golang-chi/api/resources/user"
	"golang-chi/api/router/middleware"
	"golang-chi/util/logger"
	"net/http"
	"strings"
)

func NewApp(l *logger.Logger, v *validator.Validate) *chi.Mux {
	r := chi.NewRouter()
	cors := cors.New(cors.Options{
		AllowedOrigins:   []string{"*"},
		AllowedMethods:   []string{"GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"},
		AllowedHeaders:   []string{"Accept", "Authorization", "Content-Type", "X-CSRF-Token"},
		AllowCredentials: true,
		MaxAge:           300, // Maximum value not ignored by any of major browsers
	})
	r.Use(cors.Handler)

	userAPI := user.New(l, v)
	greetingAPI := greeting.New(l, v, userAPI.Service)

	// providing versioned api endpoints should be better, but for simplicity, I'm not doing it
	r.Route("/api/user", func(r chi.Router) {
		r.Use(middleware.ContentTypeJson)
		r.Method("GET", "/", requestInterceptor.NewHandler(userAPI.List, l))
		r.Method("POST", "/", requestInterceptor.NewHandler(userAPI.Create, l))
		//r.Method("GET", "/user/{id}", requestInterceptor.NewHandler(userAPI.Read, l))
		//r.Method("PUT", "/user/{id}", requestInterceptor.NewHandler(userAPI.Update, l))
		//r.Method("DELETE", "/user/{id}", requestInterceptor.NewHandler(userAPI.Delete, l))
	})
	r.Route("/api/greeting", func(r chi.Router) {
		r.Use(middleware.ContentTypeJson)
		r.Use(middleware.BasicAuth(userAPI.GetPassword))

		r.Method("GET", "/", requestInterceptor.NewHandler(greetingAPI.Greet, l))
	})
	walkFunc := func(method string, route string, handler http.Handler, middlewares ...func(http.Handler) http.Handler) error {
		route = strings.Replace(route, "/*/", "/", -1)
		l.Debug().Str("method", method).Str("route", route).Msg("provided route")
		return nil
	}

	if err := chi.Walk(r, walkFunc); err != nil {
		l.Error().Caller().Err(err).Msg("Error while printing routes")
	}
	return r
}

func NewOps(l *logger.Logger, v *validator.Validate) *chi.Mux {
	r := chi.NewRouter()
	r.Use(middleware.ContentTypeJson)
	r.Get("/ops/start", health.Read)
	r.Get("/ops/ready", health.Read)
	r.Get("/ops/live", health.Read)
	return r
}
