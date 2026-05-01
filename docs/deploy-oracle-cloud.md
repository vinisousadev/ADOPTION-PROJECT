# Deploy na Oracle Cloud

Arquitetura recomendada:

- VM do banco Oracle: expõe a porta `1521` apenas para o IP privado da VM do backend.
- VM do backend Spring Boot: expõe a porta `8080` para quem for consumir a API, ou somente para um proxy/reverse proxy.
- As duas VMs devem ficar na mesma VCN, preferencialmente usando IP privado para a conexão backend -> banco.

## Banco de dados

Na VM do banco, instale/configure o Oracle Database e confirme:

- O listener está ativo na porta `1521`.
- O service name usado pelo backend existe, por exemplo `XEPDB1`.
- O usuário da aplicação existe, por exemplo `ADOCAO`.
- A regra de segurança da VCN libera `1521` apenas a partir do IP privado da VM do backend.

Exemplo de URL usada pelo backend:

```text
jdbc:oracle:thin:@10.0.0.20:1521/XEPDB1
```

## Backend

Na VM do backend, instale Java 21:

```bash
sudo apt update
sudo apt install -y openjdk-21-jdk
java -version
```

Envie o projeto para a VM e gere o jar:

```bash
./mvnw -DskipTests package
```

Configure as variaveis de ambiente:

```bash
export DB_HOST=10.0.0.20
export DB_PORT=1521
export DB_SERVICE_NAME=XEPDB1
export DB_USERNAME=ADOCAO
export DB_PASSWORD='senha-do-banco'
export JWT_SECRET='troque-por-uma-chave-grande-com-mais-de-32-caracteres'
```

Execute:

```bash
java -jar target/adoption-0.0.1-SNAPSHOT.jar
```

O backend sobe por padrao na porta `8080`.

## Servico systemd

Crie o arquivo `/etc/systemd/system/adoption.service`:

```ini
[Unit]
Description=Adoption Backend
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/opt/adoption
ExecStart=/usr/bin/java -jar /opt/adoption/adoption-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10
Environment=DB_HOST=10.0.0.20
Environment=DB_PORT=1521
Environment=DB_SERVICE_NAME=XEPDB1
Environment=DB_USERNAME=ADOCAO
Environment=DB_PASSWORD=senha-do-banco
Environment=JWT_SECRET=troque-por-uma-chave-grande-com-mais-de-32-caracteres

[Install]
WantedBy=multi-user.target
```

Ative o servico:

```bash
sudo systemctl daemon-reload
sudo systemctl enable adoption
sudo systemctl start adoption
sudo systemctl status adoption
```

Ver logs:

```bash
journalctl -u adoption -f
```

## Regras de rede

Na Oracle Cloud, libere:

- Backend VM: TCP `8080`, ou TCP `80/443` se usar Nginx.
- Database VM: TCP `1521` somente vindo do IP privado da Backend VM.

No Ubuntu, se `ufw` estiver ativo:

```bash
sudo ufw allow 8080/tcp
```

Na VM do banco, prefira liberar `1521` somente para o IP privado do backend:

```bash
sudo ufw allow from 10.0.0.10 to any port 1521 proto tcp
```
