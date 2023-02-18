package requestInterceptor

import (
	"golang-chi/util/logger"
	"net/http"
)

type Handler struct {
	handler http.Handler
	logger  *logger.Logger
}

func NewHandler(h http.HandlerFunc, l *logger.Logger) *Handler {
	return &Handler{
		handler: h,
		logger:  l,
	}
}

func (h *Handler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	h.handler.ServeHTTP(w, r)
}
