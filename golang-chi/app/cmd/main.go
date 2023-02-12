package main

import (
	"fmt"
	validator2 "github.com/go-playground/validator/v10"
	"golang-chi/api/router"
	"golang-chi/config"
	"golang-chi/util/logger"
	"golang-chi/util/validator"
	"net/http"
	"os"
	"os/signal"
	"syscall"
)

func main() {

	c := config.New()
	l := logger.New(c.Server.Debug)
	v := validator.New()

	opsServer(l, v, c)
	appServer(l, v, c)

	WaitForTermination(*l)

}

func WaitForTermination(logger logger.Logger) {
	sig := make(chan os.Signal, 1)
	signal.Notify(sig, syscall.SIGTERM, syscall.SIGINT)
	rec := <-sig
	logger.Info().Str("signal", rec.String()).Msg("terminating service")
}

func opsServer(l *logger.Logger, v *validator2.Validate, c *config.Conf) {
	rApp := router.NewOps(l, v)
	s := &http.Server{
		Addr:         fmt.Sprintf(":%d", c.Server.OperatingPort),
		Handler:      rApp,
		ReadTimeout:  c.Server.TimeoutRead,
		WriteTimeout: c.Server.TimeoutWrite,
		IdleTimeout:  c.Server.TimeoutIdle,
	}
	//go func() {
	//	sigint := make(chan os.Signal, 1)
	//	signal.Notify(sigint, os.Interrupt, syscall.SIGTERM)
	//	<-sigint
	//
	//	l.Info().Msgf("Shutting down operating server %v", s.Addr)
	//
	//	ctx, cancel := context.WithTimeout(context.Background(), c.Server.TimeoutIdle)
	//	defer cancel()
	//
	//	if err := s.Shutdown(ctx); err != nil {
	//		l.Error().Err(err).Msg("Operating server shutdown failure")
	//	}
	//
	//}()

	go func() {
		l.Info().Msgf("Operating starting server %v", s.Addr)
		if err := s.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			l.Fatal().Err(err).Msg("Operating server startup failure")
		}
	}()

}

func appServer(l *logger.Logger, v *validator2.Validate, c *config.Conf) {
	rApp := router.NewApp(l, v)
	s := &http.Server{
		Addr:         fmt.Sprintf(":%d", c.Server.Port),
		Handler:      rApp,
		ReadTimeout:  c.Server.TimeoutRead,
		WriteTimeout: c.Server.TimeoutWrite,
		IdleTimeout:  c.Server.TimeoutIdle,
	}
	//closed := make(chan struct{})
	//go func() {
	//	sigint := make(chan os.Signal, 1)
	//	signal.Notify(sigint, os.Interrupt, syscall.SIGTERM)
	//	<-sigint
	//
	//	l.Info().Msgf("Shutting down server %v", s.Addr)
	//
	//	ctx, cancel := context.WithTimeout(context.Background(), c.Server.TimeoutIdle)
	//	defer cancel()
	//
	//	if err := s.Shutdown(ctx); err != nil {
	//		l.Error().Err(err).Msg("Server shutdown failure")
	//	}
	//
	//	close(closed)
	//}()

	l.Info().Msgf("Starting server %v", s.Addr)
	if err := s.ListenAndServe(); err != nil && err != http.ErrServerClosed {
		l.Fatal().Err(err).Msg("Server startup failure")
	}

	//<-closed
}
