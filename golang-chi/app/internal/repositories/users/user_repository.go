package users

import (
	"errors"
	"github.com/go-playground/validator/v10"
	"github.com/google/uuid"
	"golang-chi/models"
	"golang-chi/util/logger"
)

type Repository struct {
	Users     map[string]models.UserRepo
	logger    *logger.Logger
	validator *validator.Validate
}

func NewRepository(logger *logger.Logger, validator *validator.Validate) *Repository {
	return &Repository{
		logger:    logger,
		validator: validator,
		Users:     map[string]models.UserRepo{},
	}
}

func (r *Repository) ListUsers() (models.UsersRepo, error) {
	result := models.UsersRepo{}

	if len(r.Users) == 0 {
		return result, nil
	}

	for _, user := range r.Users {
		result = append(result, &models.UserRepo{
			Login:     user.Login,
			Password:  user.Password,
			FirstName: user.FirstName,
			LastName:  user.LastName,
		})
	}
	return result, nil
}

func (r *Repository) CreateUser(user models.UserDTO) (*models.UserRepo, error) {

	if value, isMapContainsKey := r.Users[user.Login]; isMapContainsKey {
		return nil, errors.New(models.USER_ALREADY_EXISTS)
	} else {

		r.Users[user.Login] = models.UserRepo{
			Id:        uuid.New().String(),
			Login:     user.Login,
			Password:  user.Password,
			FirstName: user.FirstName,
			LastName:  user.LastName,
		}

		value, _ = r.Users[user.Login]
		return &value, nil
	}

}

func (r *Repository) GetUserByLogin(login string) (*models.UserRepo, error) {
	if value, isMapContainsKey := r.Users[login]; isMapContainsKey {
		return &value, nil
	} else {
		return nil, errors.New(models.USER_NOT_FOUND)
	}
}

//
//func (r *Repository) CreateUser(user *User) (*User, error) {
//	if err := r.db.Create(user).Error; err != nil {
//		return nil, err
//	}
//
//	return user, nil
//}
//
//func (r *Repository) GetUser(id uint) (*User, error) {
//	user := &User{}
//	if err := r.db.Where("id = ?", id).First(&user).Error; err != nil {
//		return nil, err
//	}
//
//	return user, nil
//}
//
//func (r *Repository) UpdateUser(user *User) error {
//	if err := r.db.First(&User{}, user.ID).Updates(user).Error; err != nil {
//		return err
//	}
//
//	return nil
//}
//
//func (r *Repository) DeleteUser(id uint) error {
//	user := &User{}
//	if err := r.db.Where("id = ?", id).Delete(&user).Error; err != nil {
//		return err
//	}
//
//	return nil
//}
