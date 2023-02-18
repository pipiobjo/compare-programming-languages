package user

import (
	"encoding/json"
	"fmt"
	"github.com/go-playground/validator/v10"
	e "golang-chi/api/resources/common"
	user "golang-chi/internal/services/users"
	"golang-chi/models"
	"net/http"

	"golang-chi/util/logger"
)

type API struct {
	logger    *logger.Logger
	validator *validator.Validate
	Service   *user.Service
	//repository *users.Repository
}

func New(logger *logger.Logger, validator *validator.Validate) *API {
	return &API{
		logger:    logger,
		validator: validator,
		Service:   user.New(logger, validator),
	}
}

func (a *API) GetPassword(login string) (string, error) {
	a.logger.Info().Caller().Str("login", login).Msg("getting password")
	userDTO, err := a.Service.GetUserByLogin(login)
	if err != nil {
		return "", err
	}
	return userDTO.Password, nil
}

func (a *API) Create(writer http.ResponseWriter, request *http.Request) {
	decoder := json.NewDecoder(request.Body)
	var reqUser models.CreateUserRequestUI
	err := decoder.Decode(&reqUser)
	if err != nil {
		a.logger.Error().Caller().Err(err).Msg("Error while decoding json")
		e.AppError(writer, e.JsonDecodingFailure)
	}
	dto := reqUser.ToDTO()
	createdUser, err := a.Service.CreateUser(*dto)
	if err != nil {
		if err.Error() == models.USER_ALREADY_EXISTS {
			e.BadRequest(writer, models.USER_ALREADY_EXISTS)
			return
		}
		e.AppError(writer, e.DataAccessFailure)
		return
	}
	responseUI := createdUser.ToUI()
	if err := json.NewEncoder(writer).Encode(responseUI); err != nil {
		a.logger.Error().Caller().Err(err).Msg("Error while encoding json")
		e.AppError(writer, e.JsonEncodingFailure)
		return
	}
}

func (a *API) List(w http.ResponseWriter, r *http.Request) {
	users, err := a.Service.ListUsers()
	if err != nil {
		a.logger.Error().Caller().Err(err).Msg("Error while reading users from db")
		e.AppError(w, e.DataAccessFailure)
		return
	}

	if users == nil {
		fmt.Fprint(w, "[]")
		return
	}

	if err := json.NewEncoder(w).Encode(users.ToUI()); err != nil {
		a.logger.Error().Caller().Err(err).Msg("Error while encoding json")
		e.AppError(w, e.JsonEncodingFailure)
		return
	}
}
