package common

import (
	"fmt"
	"net/http"
)

const (
	DataCreationFailure = "data creation failure"
	DataAccessFailure   = "data access failure"
	DataUpdateFailure   = "data update failure"
	DataDeletionFailure = "data deletion failure"

	JsonEncodingFailure = "json encoding failure"
	JsonDecodingFailure = "json decoding failure"

	FormErrResponseFailure = "form error response failure"

	InvalidIdInUrlParam = "invalid id in url param"
)

func AppError(w http.ResponseWriter, msg string) {
	w.WriteHeader(http.StatusInternalServerError)
	fmt.Fprintf(w, `{"error": "%v"}`, msg)
}

func ValError(w http.ResponseWriter, msg string) {
	w.WriteHeader(http.StatusUnprocessableEntity)
	fmt.Fprintf(w, `{"error": "%v"}`, msg)
}

func FormValErrors(w http.ResponseWriter, reps []byte) {
	w.WriteHeader(http.StatusUnprocessableEntity)
	w.Write(reps)
}

func BadRequest(w http.ResponseWriter, msg string) {
	w.WriteHeader(http.StatusBadRequest)
	fmt.Fprintf(w, `{"error": "%v"}`, msg)
}
