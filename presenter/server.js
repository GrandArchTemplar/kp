'use strict';

const express = require('express');
const path = require("path");

// Constants
const PORT = 28080;
const HOST = '0.0.0.0';

// App
const app = express();
app.get('/', (req, res) => {
    res.sendFile(__dirname +  '/index.html');
});

app.get('/jquery.csv.js',function(req,res){
    res.sendFile(path.join(__dirname + '/jquery.csv.js'));
});

app.get('/jquery.js',function(req,res){
    res.sendFile(path.join(__dirname + '/jquery.js'));
});

app.get('/jquery.dropdown.js',function(req,res){
    res.sendFile(path.join(__dirname + '/jquery.dropdown.js'));
});

app.get('/jquery.dropdown.css',function(req,res){
    res.sendFile(path.join(__dirname + '/jquery.dropdown.css'));
});

app.get('/mock.js',function(req,res){
    res.sendFile(path.join(__dirname + '/mock.js'));
});

app.listen(PORT, HOST);
console.log(`Running on http://${HOST}:${PORT}`);