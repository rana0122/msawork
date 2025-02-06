FROM mariadb:10.6.20
ENV MYSQL_ROOT_PASSWORD=test1234
ENV MYSQL_DATABASE=mydb
COPY ./mysql /var/lib/mysql
RUN chown -R mysql:mysql /var/lib/mysql
EXPOSE 3306
