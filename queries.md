
### INSERT 

*1*

> - [x] **Регистрация**

*Проверка существующего логина*

*2*
> - [x] **Добавление категории**

*Проверка существующей категории* 

*Category*  
                        `name`

*3*
> - [x] **Добавление конкретного оборудования**

*ClassroomsEquipment* 

                - inventory number,
                - вид оборудования(equipment),
                - аудитория (classroom)
                - номер в аудитории (number in classroom)
                - тип пренадлежности оборудования (equipment type)

*4*
> - [x] **Добавление оборудования**

*Equipment*
                        
                - описание (description)
                - категория (category)

*5*

> - [ ] **Добавление записи в учёт**

*Inventory*

                - инвентарный номер
                - дата получения
                - документ
                - ИФО
                - для какой аудитории
                - кто ответственный

*6*

> - [ ] **Добавление комментария**

*Comments*

                - учет
                - кто оставил комментарий
                - комментарий
                - дата & время оставления комментария

*7*

> - [ ] **Добавление аудитории**

*Classrooms*

### SELECT

*1*
> - [x] **Авторизация**

*Users*

                - логин
                - пароль

*2*
> - [x] **Получение категории**

*Categories*

                - select name from categories

*3*
> - [x] **Получение аудитории**

*Classrooms*

                - select number from classrooms

*4*
> - [x] **Получение конкретного оборудования**

*ClassroomsEquipment*

                - select inventory_number, number_in_classroom ...
                    where equipment = 'ПК', classroom = '120'

*5*
> - [x] **Получение комментариев**

*Comments*

                - select inventory_number, comment, user , timestamp, equipment

*6*

> - [ ] **Получение информации об оборудовании**

*Equipment*

                - select category, description

*7*

> - [ ] **Получение данных учета**

*Inventory*

                - select *

*8*
> - [x] **Получить список аудиторий по id преподавателя**

*Classrooms*


*9*
> - [x] **Получение комментариев по Учету**

*Comments*
                            
                        by InventoryId


###DELETE

*1*

> - [x] **Удаление категории**

*Categories*

*2*

> - [x] **Удаление аудитории**

*Classrooms*

*3*

> - [x] **Удаление конкретного оборудования**

*ClassroomsEquipments*

*4*

> - [x] **Удаление комментария**

*Comments*

*5*

> - [x] **Удаление оборудования**

*Equipments*

*6*

> - [x] **Удаление данных учёта**

*Inventory*


###UPDATE

> - [x] **Изменение данных пользователя**

*Users*

> - [x] **Изменение данных конкретного оборудования**

*ClassroomEquipment*

> - [x] **Изменить пароль пользователя**

*Users*

###Все запросы:
```
GET /api/v1/classroom-equipment
GET /api/v1/classroom-equipment/{id}
GET /api/v1/classrooms
GET /api/v1/classrooms/{user-id}
GET /api/v1/comments
GET /api/v1/comments/{inventory-id}}
GET /api/v1/categories
GET /api/v1/equipment
GET /api/v1/equipment/{id}
GET /api/v1/users/{id}
GET /api/v1/refresh
GET /api/v1/inventory
GET /api/v1/ifo
GET /api/v1/documents

POST /api/v1/login
POST /api/v1/sign_up
POST /api/v1/equipment
POST /api/v1/classrom-equipment
POST /api/v1/ifo
POST /api/v1/documents

PUT /api/v1/users
PUT /api/v1/users/change-password
PUT /api/v1/classroom-equipment
PUT /api/v1/ifo
PUT /api/v1/documents

DELETE /api/v1/categories/{id}
DELETE /api/v1/classroom-equipment/{id}
DELETE /api/v1/classrooms/{classroom-number}
DELETE /api/v1/comments/{id}
DELETE /api/v1/equipment/{id}
DELETE /api/v1/inventory/{id}
DELETE /api/v1/users/{id}
DELETE /api/v1/ifo/{id}
DELETE /api/v1/documents/{id}