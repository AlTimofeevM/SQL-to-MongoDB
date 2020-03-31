# SQL-to-MongoDB

Транслирует SQL запрос в запрос MongoDB </br>
Примеры:                                </br>

SELECT * FROM sales LIMIT 10                                    =====>       db.sales.find({}).limit(10)
SELECT * FROM collection WHERE width < 100 AND height = 50      =====>       db.collection.find({width: { $lt: 100 }, height: 50})
SELECT name, surname FROM players WHERE age > 20 AND score < 100 SKIP 3 LIMIT 6     =====>   db.players.find({age: { $gt: 20 }, score: { $lt: 100 }}, {name: 1, surname: 1}).skip(3).limit(6)