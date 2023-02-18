package models

type GreetingMessage struct {
	Message   string `json:"msg"`
	FirstName string `json:"firstname"`
	LastName  string `json:"lastname"`
}
