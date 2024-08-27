const express = require('express');
const router = express.Router();
const admin = require('../config/firebase');

// Пример маршрута для работы с запросами
router.get('/request', (req, res) => {
  res.send('Request route');
});

module.exports = router;
