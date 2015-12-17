#!/usr/bin/env bash

nohup java -cp ./libs/*:./conf com.example.YourApplication > /dev/null 2>&1&
