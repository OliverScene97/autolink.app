const express = require('express');
const router = express.Router();
const admin = require('firebase-admin');

const db = admin.database();

// Маршрут для создания профиля
router.post('/create', async (req, res) => {
    const { userId, nickName, avatarUrl, carCards } = req.body;

    // Проверка обязательных полей
    if (!userId || !nickName) {
        return res.status(400).json({ error: 'userId и nickName обязательны' });
    }

    try {
        // Сначала сохраняем основной профиль пользователя
        const userProfileRef = db.ref('users').child(userId);
        await userProfileRef.set({
            nickName: nickName,
            avatarUrl: avatarUrl || null
        });

        // Сохраняем карточки автомобилей отдельно в узле "carCards"
        if (carCards && carCards.length > 0) {
            const carCardsRef = db.ref('carCards').child(userId); // Сохраняем карточки под userId

            for (const card of carCards) {
                console.log('Saving car card:', card); // Отладка: вывод данных карточки перед сохранением
                const newCardRef = carCardsRef.push(); // Генерируем уникальный ключ для каждой карточки
                await newCardRef.set({
                    carImageUrl: card.carImageUrl || null,
                    carName: card.carName || null,
                    carModel: card.carModel || null,
                    carYear: card.carYear || null,
                    vinNumber: card.vinNumber || null,
                    engine: card.engine || null,
                    transmission: card.transmission || null
                });
            }
        }

        res.status(201).json({ message: 'Профиль и карточки успешно созданы' });
    } catch (error) {
        console.error('Ошибка при создании профиля:', error);
        res.status(500).json({ error: 'Ошибка при создании профиля' });
    }
});

// Маршрут для получения профиля вместе с карточками автомобилей
router.get('/:userId', async (req, res) => {
    const { userId } = req.params;

    try {
        const userProfileRef = db.ref('users').child(userId);
        const userProfileSnapshot = await userProfileRef.once('value');

        if (userProfileSnapshot.exists()) {
            const profileData = userProfileSnapshot.val();

            // Получаем связанные с пользователем карточки автомобилей
            const carCardsRef = db.ref('carCards').child(userId);
            const carCardsSnapshot = await carCardsRef.once('value');

            const carCards = carCardsSnapshot.exists() ? Object.values(carCardsSnapshot.val()) : [];

            const response = {
                ...profileData,
                carCards: carCards
            };

            res.status(200).json(response);
        } else {
            res.status(404).json({ error: 'Профиль не найден' });
        }
    } catch (error) {
        console.error('Ошибка при получении профиля:', error);
        res.status(500).json({ error: 'Ошибка при получении профиля' });
    }
});

module.exports = router;
