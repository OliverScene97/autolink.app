const admin = require('firebase-admin');

const serviceAccount = require('../mecha-6b121-firebase-adminsdk-oqfgh-96e9047dd2.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: 'https://mecha-6b121-default-rtdb.europe-west1.firebasedatabase.app/'
});

module.exports = admin;
