package config

import (
	"github.com/joeshaw/envdecode"
	"log"
	"time"
)

type Conf struct {
	Server ConfServer
}

type ConfServer struct {
	Port          int           `env:"APP_SERVER_PORT,default=8080"`
	OperatingPort int           `env:"OPS_SERVER_PORT,default=8081"`
	TimeoutRead   time.Duration `env:"SERVER_TIMEOUT_READ,default=5s"`
	TimeoutWrite  time.Duration `env:"SERVER_TIMEOUT_WRITE,,default=10s"`
	TimeoutIdle   time.Duration `env:"SERVER_TIMEOUT_IDLE,default=10s"`
	Debug         bool          `env:"SERVER_DEBUG,default=true"`
}

// parse environment variables and return a config struct
func New() *Conf {
	var c Conf
	if err := envdecode.StrictDecode(&c); err != nil {
		log.Fatalf("Failed to decode: %s", err)
	}

	return &c
}
