СЕРВИС ОБЛАЧНОГО ХРАНИЛИЩА
===
ОПИСАНИЕ
-
Приложение для хранения файлов на сервере

НАСТРОЙКА И ЗАПУСК
-
Для запуска приложения в Docker контейнере запустить файл docker-compose.yml
База данных создается в одном контейнере с приложением, не требует дополнительных настроек
Приложение будет доступно по адресу http://localhost:5050

ИНСТРУКЦИЯ
-
>Создание аккаунта пользователя

POST /cloud/signin

параметры:

|наименование|обязательный|расположение|примечание|
|-|-|-|-|
|login|true|request body|уникальное значение|
|password|true|request body|

Response:\
200 - OK\
400 - BadCredentials

>Вход пользователя в систему

POST /cloud/login

параметры:

|наименование|обязательный|расположение|примечание|
|-|-|-|-|
|login|true|request body||
|password|true|request body|

Response:\
200 { auth-token: some_token }\
400 BadCredentials

>Выход пользователя из системы

POST /cloud/login

параметры:

| наименование |обязательный| расположение |примечание|
|--------------|-|--------------|-|
| auth-token   |true| headers      ||

Response:\
200 Ok\
400 BadCredentials\
401 Unauthorized

>Загрузка файла в облако

POST /cloud/file

параметры:

| наименование |обязательный| расположение | примечание                                            |
|--------------|-|--------------|-------------------------------------------------------|
| filename     |true| query        | наименование файла передается без указания расширения |
| auth-token   |true| headers      |

Response:\
200 Ok\
400 BadCredentials\
401 Unauthorized\
500 InternalServerError

>Получение файла из облака

GET /cloud/file

параметры:

| наименование |обязательный| расположение | примечание                                           |
|--------------|-|--------------|------------------------------------------------------|
| filename     |true| query        | наименование файла передается с указанием расширения |
| auth-token   |true| headers      |

Response:\
200 Ok\
400 BadCredentials\
401 Unauthorized\
500 InternalServerError

>Переименование файла

PUT /cloud/file

параметры:

| наименование |обязательный| расположение | примечание                                           |
|--------------|-|--------------|------------------------------------------------------|
| filename     |true| query        | наименование файла передается с указанием расширения |
|name|true|requert body| новое наименование передается без расширения         |
| auth-token   |true| headers      |

Response:\
200 Ok\
400 BadCredentials\
401 Unauthorized\
500 InternalServerError

>Удаление файла

DELETE /cloud/file

параметры:

| наименование |обязательный| расположение | примечание                                           |
|--------------|-|--------------|------------------------------------------------------|
| filename     |true| query        | наименование файла передается с указанием расширения |
| auth-token   |true| headers      |

Response:\
200 Ok\
400 BadCredentials\
401 Unauthorized\
500 InternalServerError

>Получение списка файлов

DELETE /cloud/list

параметры:

| наименование |обязательный| расположение | примечание                 |
|--------------|-|--------------|----------------------------|
| limit        |true| query        | количество выводимых строк |
| auth-token   |true| headers      |

Response:\
200 Ok [{ filename: file.txt, size: 1245 }] \
400 BadCredentials\
401 Unauthorized\
500 InternalServerError