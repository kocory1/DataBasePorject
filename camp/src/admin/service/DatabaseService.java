package admin.service;

import java.sql.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 데이터베이스 초기화 서비스
 * ini.sql 파일을 읽어서 실행
 */
public class DatabaseService {
    private Connection connection;
    private static final String SQL_FILE_PATH = "ini.sql";

    public DatabaseService(Connection connection) {
        this.connection = connection;
    }

    /**
     * ini.sql 파일 실행
     * @return 성공 여부
     * @throws Exception 실행 중 오류
     */
    public boolean executeInitScript() throws Exception {
        // 1. 파일 확인
        File sqlFile = new File(SQL_FILE_PATH);
        if (!sqlFile.exists()) {
            throw new FileNotFoundException("ini.sql 파일을 찾을 수 없습니다: " + sqlFile.getAbsolutePath());
        }

        // 2. SQL 파일 파싱
        List<String> sqlStatements = parseSqlFile();

        // 3. SQL 실행
        for (String sql : sqlStatements) {
            executeStatement(sql);
        }

        return true;
    }

    /**
     * SQL 파일을 파싱하여 개별 SQL 문으로 분리
     */
    private List<String> parseSqlFile() throws IOException {
        List<String> statements = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(SQL_FILE_PATH))) {
            StringBuilder currentStatement = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // 빈 줄이나 주석 건너뛰기
                if (line.isEmpty() || line.startsWith("--") || line.startsWith("#")) {
                    continue;
                }

                currentStatement.append(line).append(" ");

                // 세미콜론으로 문장 끝 판단
                if (line.endsWith(";")) {
                    String statement = currentStatement.toString().trim();
                    if (!statement.isEmpty()) {
                        statements.add(statement);
                    }
                    currentStatement.setLength(0);
                }
            }
        }
        return statements;
    }

    /**
     * 개별 SQL 문 실행
     */
    private void executeStatement(String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
}