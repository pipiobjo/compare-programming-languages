package greeting

import (
	"github.com/go-playground/validator/v10"
	user "golang-chi/internal/services/users"
	"golang-chi/models"
	"golang-chi/util/logger"
)

type Service struct {
	logger      *logger.Logger
	validator   *validator.Validate
	userService *user.Service
}

func New(logger *logger.Logger, validator *validator.Validate, userService *user.Service) *Service {
	return &Service{
		logger:      logger,
		validator:   validator,
		userService: userService,
	}
}

func (s Service) GreetUser(login string) (*models.GreetingMessage, error) {
	userDTO, err := s.userService.GetUserByLogin(login)
	if err != nil {
		return nil, err
	}

	return &models.GreetingMessage{
		Message:   "Hello " + userDTO.FirstName + " " + userDTO.LastName + "!",
		FirstName: userDTO.FirstName,
		LastName:  userDTO.LastName,
	}, nil
}
