# JaCoCo Code Coverage Implementation & User Guide

**Document Scope:** Complete guide to JaCoCo code coverage configuration, metrics, report generation, and CI/CD integration for the Student Placement Management System.  
**Specification Reference:** PRP Phase 1 & Phase 4  
**Date of Verification:** 2026-10-28  

---

## 1. Overview & Tool Selection

JaCoCo (Java Code Coverage) is integrated into the Spring Boot backend build system to generate deterministic instruction, branch, line, and method coverage reports on every test run.

### Version Compatibility Matrix
| Component | Version | Compatibility Rationale |
| :--- | :--- | :--- |
| **Java SDK** | Oracle Java 21 LTS | Target compilation and execution runtime |
| **JaCoCo Plugin** | `0.8.12` | Official stable release with full Java 21 classfile bytecode support |
| **Spring Boot** | 4.1.2 | Maven plugin lifecycle integration |
| **Surefire Plugin** | Maven Surefire 3.5.2 | Captures runtime execution bytecode events |

---

## 2. Maven Configuration (`backend/pom.xml`)

JaCoCo is configured as a build plugin in [backend/pom.xml](file:///d:/Student-Placement-Management-System/backend/pom.xml) under `<build><plugins>`:

```xml
<!-- JaCoCo code coverage plugin -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Execution Goals Explained:
1. **`prepare-agent`**: Automatically attaches the JaCoCo Java runtime agent JVM arguments before the Maven Surefire plugin launches the JUnit test runner. Execution bytecode hits are recorded to `target/jacoco.exec`.
2. **`report`**: Bound to the `test` phase. It parses the binary `jacoco.exec` file alongside compiled `.class` files in `target/classes` and generates human-readable HTML and machine-readable XML reports.

---

## 3. How to Run JaCoCo Locally

From the project root or backend directory, execute:

```powershell
cd D:\Student-Placement-Management-System\backend
.\mvnw.cmd clean verify
```

Alternatively, to run tests and explicitly generate the report:
```powershell
.\mvnw.cmd clean test jacoco:report
```

---

## 4. Generated Report Locations

Following a successful build, reports are created in:

| Format | File Location | Purpose |
| :--- | :--- | :--- |
| **Interactive HTML** | `backend/target/site/jacoco/index.html` | Browsable web report displaying line-by-line coverage, highlighting covered lines in green and missed lines in red |
| **Standard XML** | `backend/target/site/jacoco/jacoco.xml` | Machine-readable report consumed by SonarQube and CI/CD tools |
| **Binary Data** | `backend/target/jacoco.exec` | Raw agent bytecode execution execution data |
| **Surefire XML** | `backend/target/surefire-reports/*.xml` | Individual JUnit test suite result XMLs consumed by Jenkins |

---

## 5. Verified Code Coverage Results

Below are the verified metrics extracted from the actual [jacoco.xml](file:///d:/Student-Placement-Management-System/backend/target/site/jacoco/jacoco.xml):

| Metric | Missed | Covered | Total | Coverage Percentage |
| :--- | :---: | :---: | :---: | :---: |
| **Class Coverage** | 0 | 12 | 12 | **100.00%** |
| **Branch Coverage** | 0 | 22 | 22 | **100.00%** |
| **Line Coverage** | 6 | 234 | 240 | **97.50%** |
| **Instruction Coverage** | 50 | 892 | 942 | **94.69%** |
| **Method Coverage** | 8 | 94 | 102 | **92.16%** |
| **Cyclomatic Complexity** | 8 | 105 | 113 | **92.92%** |

### Classes Analyzed (12 Bundled Classes)
1. `com.placement.backend.BackendApplication`
2. `com.placement.backend.HomeController`
3. `com.placement.backend.config.WebConfig`
4. `com.placement.backend.controller.StudentController`
5. `com.placement.backend.controller.CompanyController`
6. `com.placement.backend.controller.PlacementDriveController`
7. `com.placement.backend.controller.ApplicationController`
8. `com.placement.backend.entity.Student`
9. `com.placement.backend.entity.Company`
10. `com.placement.backend.entity.PlacementDrive`
11. `com.placement.backend.entity.Application`
12. `com.placement.backend.exception.GlobalExceptionHandler`

---

## 6. SonarQube Integration

SonarQube automatically ingests the JaCoCo XML report via the `sonar-maven-plugin`.

### Integration Property
In `pom.xml`:
```xml
<sonar.coverage.jacoco.xmlReportPaths>target/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
```

### Execution Command
```powershell
.\mvnw.cmd jacoco:report sonar:sonar `
  -Dsonar.token=$env:SONAR_TOKEN `
  -Dsonar.host.url=http://localhost:9000
```
- **SonarQube Quality Gate:** **PASSED (OK)**
- **Reported Coverage in SonarQube:** **97.7%**

---

## 7. Jenkins CI/CD Archival

In [Jenkinsfile](file:///d:/Student-Placement-Management-System/Jenkinsfile), JaCoCo artifacts and JUnit results are archived automatically after the test execution stage:

```groovy
post {
    always {
        junit testResults: 'backend/target/surefire-reports/*.xml', allowEmptyResults: true
        archiveArtifacts artifacts: 'backend/target/site/jacoco/**', allowEmptyArchive: true, fingerprint: true
    }
}
```
This guarantees that the complete interactive HTML coverage report is accessible directly from the Jenkins build artifacts menu without requiring third-party plugins.
