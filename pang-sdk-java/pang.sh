#!/usr/bin/env bash

nohup java -cp ./libs/*:./conf com.pangdata.client.example.PangTaskTimerExample > /dev/null 2>&1&
