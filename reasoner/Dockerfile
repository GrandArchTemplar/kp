# используем легковесный образ линукса c предустановленным питоном как основу
FROM python:3.9.1

# устанавливаем все необходимое
RUN apt-get update && apt-get install -y gfortran g++ musl-dev \
    && pip install --upgrade --ignore-installed pip
#RUN apt-get install -y jpeg-dev zlib-dev
RUN pip install --ignore-installed pillow

RUN pip install torch==1.7.1+cpu \
                    torchvision==0.8.2+cpu \
                    torchaudio===0.7.2 -f https://download.pytorch.org/whl/torch_stable.html \
                    Pillow==6.2.2 \
                    numpy \
                    scipy \
                    sklearn \
                    flask

# копируем содержимое текущей папки в контейнер, в директорию app
COPY . /app

# переходим в директорию app
WORKDIR /app

# делаем порт 8955 общедоступным извне
EXPOSE 8955

# по умолчанию запускаем интерпретатор питона
ENTRYPOINT ["python", "main.py"]

