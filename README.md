## Requesitos para operar:
**Baixe:**
   - Apache Netbeans Studio\n
   - pgAdmin4
   - arquivo .jar "postgresql-42.7.5.jar"

## ⚙️ Como executar o projeto

1. **Importe o projeto**
   - Abra o **Apache NetBeans Studio**
   - Vá em **File > Open Project** e selecione a pasta `projeto`

2. **Configure o banco de dados**
   - Abra o **PgAdmin 4**
   - Crie um **servidor** com:
     ```
     usuário: postgres
     senha: postgres
     ```
   - No **Query Tool**, copie e cole o conteúdo do arquivo `sql.txt` e execute.

3. **Verifique as dependências**
   - No NetBeans, abra o pacote **Libraries**
   - Verifique se há o arquivo:
     ```
     postgresql-42.7.5.jar
     ```
   - Caso falte algum `.jar`, clique com o botão direito no pacote **Libraries**
     → selecione **Add JAR/Folder...**
     → adicione o(s) arquivo(s) correspondente(s).

4. **Execute o projeto**
   - Clique com o botão direito sobre o projeto → **Run**
   - Se o sistema solicitar credenciais:
     ```
     usuário: admin
     senha: Futebol
     ```

---
