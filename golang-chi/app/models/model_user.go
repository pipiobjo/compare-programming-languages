package models

type CreateUserRequestUI struct {
	Login     string `json:"login"`
	Password  string `json:"password"`
	FirstName string `json:"firstName"`
	LastName  string `json:"lastName"`
}

func (u CreateUserRequestUI) ToDTO() *UserDTO {
	return &UserDTO{
		Login:     u.Login,
		Password:  u.Password,
		FirstName: u.FirstName,
		LastName:  u.LastName,
	}
}

type CreateUserResponseUI struct {
	id string `json:"id"`
}

type CreateUserResponseDTO struct {
	Login     string `json:"login"`
	Password  string `json:"password"`
	Id        string `json:"id"`
	FirstName string `json:"firstName"`
	LastName  string `json:"lastName"`
}

func (u CreateUserResponseDTO) ToUI() *CreateUserResponseUI {
	return &CreateUserResponseUI{
		id: u.Id,
	}
}

type UserUI struct {
	Login     string `json:"login"`
	FirstName string `json:"firstName"`
	LastName  string `json:"lastName"`
}

type UsersDTO []*UserDTO

func (u UsersDTO) ToUI() []*UserUI {
	result := make([]*UserUI, 0)

	for _, user := range u {
		result = append(result, user.ToUI())
	}
	return result
}

type UserDTO struct {
	Login     string `json:"login"`
	Password  string `json:"password"`
	FirstName string `json:"firstName"`
	LastName  string `json:"lastName"`
}

func (d UserDTO) ToUI() *UserUI {
	return &UserUI{
		Login:     d.Login,
		FirstName: d.FirstName,
		LastName:  d.LastName,
	}
}

type UsersRepo []*UserRepo

type UserRepo struct {
	Login     string `json:"login"`
	Password  string `json:"password"`
	Id        string `json:"id"`
	FirstName string `json:"firstName"`
	LastName  string `json:"lastName"`
}

func (r UserRepo) ToDTO() *UserDTO {
	return &UserDTO{
		Login:     r.Login,
		Password:  r.Password,
		FirstName: r.FirstName,
		LastName:  r.LastName,
	}
}

type CreateUserUIReq struct {
	Login     string `json:"login"`
	Password  string `json:"password"`
	FirstName string `json:"firstName"`
	LastName  string `json:"lastName"`
}
