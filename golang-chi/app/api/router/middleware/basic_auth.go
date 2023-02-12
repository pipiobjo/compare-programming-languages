package middleware

import (
	"context"
	"crypto/subtle"
	"fmt"
	"net/http"
)

type GetCredentials func(login string) (string, error)

// BasicAuth implements a simple middleware handler for adding basic http auth to a route.
func BasicAuth(creds GetCredentials) func(next http.Handler) http.Handler {
	realm := "Restricted"
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			user, pass, ok := r.BasicAuth()
			if !ok {
				basicAuthFailed(w, realm)
				return
			}

			credPass, err := creds(user)
			if err != nil || subtle.ConstantTimeCompare([]byte(pass), []byte(credPass)) != 1 {
				basicAuthFailed(w, realm)
				return
			}
			// set the user login in the context
			ctx := context.WithValue(r.Context(), "login", user)
			newR := r.WithContext(ctx)

			// serve the next handlers
			next.ServeHTTP(w, newR)
		})
	}
}

func basicAuthFailed(w http.ResponseWriter, realm string) {
	w.Header().Add("WWW-Authenticate", fmt.Sprintf(`Basic realm="%s"`, realm))
	w.WriteHeader(http.StatusUnauthorized)
}
