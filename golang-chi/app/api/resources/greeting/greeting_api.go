package greeting

import (
	"encoding/json"
	"github.com/go-playground/validator/v10"
	e "golang-chi/api/resources/common"
	"golang-chi/internal/services/greeting"
	"golang-chi/internal/services/users"
	"golang-chi/util/logger"
	"net/http"
)

type API struct {
	logger    *logger.Logger
	validator *validator.Validate
	service   *greeting.Service
}

func New(logger *logger.Logger, validator *validator.Validate, userService *users.Service) *API {
	return &API{
		logger:    logger,
		validator: validator,
		service:   greeting.New(logger, validator, userService),
	}
}

func (a *API) Greet(w http.ResponseWriter, r *http.Request) {
	var userLogin string = r.Context().Value("login").(string)
	a.logger.Info().Caller().Str("login", userLogin).Msg("listing users")
	greeting, err := a.service.GreetUser(userLogin)
	if err != nil {
		a.logger.Error().Caller().Err(err).Msg("")
		e.AppError(w, e.DataAccessFailure)
		return
	}

	if err := json.NewEncoder(w).Encode(greeting); err != nil {
		a.logger.Error().Caller().Err(err).Msg("Error encoding greeting message to JSON")
		e.AppError(w, e.JsonEncodingFailure)
		return
	}
}
