package users

import (
	"github.com/go-playground/validator/v10"
	repo "golang-chi/internal/repositories/users"
	"golang-chi/models"
	"golang-chi/util/logger"
)

type Service struct {
	logger     *logger.Logger
	validator  *validator.Validate
	repository *repo.Repository
}

func New(logger *logger.Logger, validator *validator.Validate) *Service {
	return &Service{
		logger:     logger,
		validator:  validator,
		repository: repo.NewRepository(logger, validator),
	}
}

func (a *Service) ListUsers() (models.UsersDTO, error) {

	users, err := a.repository.ListUsers()
	if err != nil {
		a.logger.Error().Caller().Err(err).Msg("error while listing users")
		return nil, err
	}

	result := models.UsersDTO{}
	for _, user := range users {
		result = append(result, user.ToDTO())
	}
	return result, nil
}

func (a *Service) CreateUser(user models.UserDTO) (*models.CreateUserResponseDTO, error) {
	createUser, err := a.repository.CreateUser(user)
	if err != nil {
		a.logger.Error().Caller().Err(err).Msg("error while creating user")
		return nil, err
	}
	return &models.CreateUserResponseDTO{
		Id: createUser.Id,
	}, nil
}

func (a *Service) GetUserByLogin(login string) (*models.UserDTO, error) {
	user, err := a.repository.GetUserByLogin(login)
	if err != nil {
		a.logger.Error().Caller().Err(err).Msg("error while getting user")
		return nil, err
	}
	return user.ToDTO(), nil
}
