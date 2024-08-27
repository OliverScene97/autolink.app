const express = require('express');
const app = express();
const admin = require('./config/firebase');
app.use(express.json());



const profileRoutes = require('./routes/profileRoutes');
const requestRoutes = require('./routes/requestRoutes');

app.use('/api/profile', profileRoutes);
app.use('/api/request', requestRoutes);

app.get('/', (req, res) => {
  res.send('Hello from Node.js and Firebase!');
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
