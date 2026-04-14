# Upgrade Plan: Online Banking Desktop (20260402062855)

- **Generated**: 2026-04-02 at 06:28:55 UTC
- **HEAD Branch**: main
- **HEAD Commit ID**: N/A (git available)

## Available Tools

**JDKs**
- JDK 21.0.8: C:\Program Files\Java\jdk-21\bin (current project JDK, used by step 2)
- JDK 25: **C:\Users\Darshan\.jdk\jdk-25** (installed, target version for steps 3-5)

**Build Tools**
- Maven 3.9.14: **C:\Users\Darshan\.maven\maven-3.9.14\bin** (installed, compatible with Java 25)
- No Maven Wrapper present (project uses system Maven)

**Build Plugins** (all compatible with Java 25)
- maven-compiler-plugin: 3.11.0 ✓
- maven-surefire-plugin: 3.1.2 ✓
- javafx-maven-plugin: 0.0.8 ✓

## Guidelines

- Upgrade all compatible dependencies (esp. JavaFX) to match Java 25
- Maintain backward compatibility with existing codebase logic
- Verify compilation of both main source and test code
- Run full test suite to ensure no regressions
- Document any known issues or limitations

## Options

- Working branch: appmod/java-upgrade-20260402062855
- Run tests before and after the upgrade: true

## Upgrade Goals

- **Primary Goal**: Upgrade Java from 21 LTS to 25 LTS (latest available LTS release)
- **Secondary Goal**: Update JavaFX to version 25.0.x for compatibility with Java 25
- **Target Support**: Extend LTS support until September 2033 (Java 25 support period)

### Technology Stack

| Technology/Dependency | Current | Min Compatible | Why Incompatible |
| --------------------- | ------- | -------------- | ---------------- |
| Java (Compiler) | 21 | 25 | User requested |
| JavaFX | 21.0.1 | 25.0.x | JavaFX version must match Java runtime |
| JUnit Jupiter | 5.10.1 | 5.10.1+ | Compatible, optional minor upgrade |
| SLF4J | 2.0.12 | 2.0.12+ | Compatible |
| H2 Database | 2.2.224 | 2.2.224+ | Compatible |
| HikariCP | 5.1.0 | 5.1.0+ | Compatible |
| java-dotenv | 5.2.2 | 5.2.2+ | Compatible |
| maven-compiler-plugin | 3.11.0 | 3.11.0+ | Compatible with Java 25 |
| maven-surefire-plugin | 3.1.2 | 3.1.2+ | Compatible |
| javafx-maven-plugin | 0.0.8 | 0.0.8+ | Compatible |

### Derived Upgrades

1. **JavaFX 21.0.1 → 25.0.x** (REQUIRED)
   - JavaFX is Java version-specific; each Java release requires matching JavaFX version
   - OpenJFX provides JavaFX 25.0.x for Java 25 (released September 2025)
   - No breaking API changes between JavaFX 21 and 25; migration requires version alignment only

2. **Optional: maven-compiler-plugin upgrade to 3.12.0+** (recommended)
   - Latest version provides enhanced Java 25 support and performance optimizations
   - Backward compatible; no code changes required

3. **Optional: JUnit 5.10.1 → 5.10.x or 5.11.x** (recommended)
   - Latest 5.x versions include new test features and Java 25 compatibility improvements
   - Backward compatible with existing tests; no test code changes required

## Upgrade Steps

### Step 1: Setup Environment

**Rationale**: Install and verify JDK 25 and Maven 3.9.14 are ready for build.

**Changes to Make**:
- [ ] Verify JDK 25 installed at C:\Users\Darshan\.jdk\jdk-25
- [ ] Verify Maven 3.9.14 installed at C:\Users\Darshan\.maven\maven-3.9.14\bin
- [ ] Set JAVA_HOME environment variable to Java 25 path
- [ ] Verify Maven can access Java 25

**Verification**:
- Command: `mvn -v` → Should report Maven 3.9.14 and Java 25
- JDK: Java 25
- Expected Result: Environment setup succeeds, no compilation in this step

---

### Step 2: Setup Baseline

**Rationale**: Establish pre-upgrade build and test baseline with Java 21 for comparison.

**Changes to Make**:
- [ ] Stash any uncommitted changes via version control
- [ ] Switch to Java 21.0.8 for baseline build
- [ ] Run baseline compilation: `mvn clean compile test-compile`
- [ ] Run baseline tests: `mvn clean test`
- [ ] Document test pass rate

**Verification**:
- Command: `mvn clean compile test-compile && mvn clean test`
- JDK: Java 21.0.8
- Expected Result: Document baseline build result and test pass/fail counts (establishes acceptance criteria)

---

### Step 3: Upgrade Java Compiler Release Target to 25

**Rationale**: Update pom.xml to target Java 25 for compilation.

**Changes to Make**:
- [ ] Update `pom.xml` property `<maven.compiler.release>21</maven.compiler.release>` → `<maven.compiler.release>25</maven.compiler.release>`
- [ ] Optionally upgrade `maven-compiler-plugin` from 3.11.0 to 3.12.0+ (for latest Java 25 support)
- [ ] Verify all related build configurations support Java 25

**Files Modified**:
- `pom.xml`: Update `<properties>` section with new Java release version

**Verification**:
- Command: `mvn clean compile test-compile`
- JDK: Java 25
- Expected Result: All main and test code compiles successfully

---

### Step 4: Upgrade JavaFX to Version 25

**Rationale**: Update JavaFX version to 25.0.x for Java 25 runtime compatibility. JavaFX must match Java runtime version.

**Changes to Make**:
- [ ] Update `pom.xml` property `<javafx.version>21.0.1</javafx.version>` → `<javafx.version>25.0.x</javafx.version>` (use latest 25.x available)
- [ ] Verify all JavaFX dependencies use updated version (javafx-controls, javafx-fxml)
- [ ] Optionally update javafx-maven-plugin to 0.0.8+ if newer available

**Files Modified**:
- `pom.xml`: Update `<properties>` section with new JavaFX version

**Verification**:
- Command: `mvn clean compile test-compile`
- JDK: Java 25
- Expected Result: Compilation succeeds, JavaFX dependencies resolve correctly

**Known Issues**:
- None expected; JavaFX 25 maintains API compatibility with JavaFX 21

---

### Step 5: Final Validation

**Rationale**: Comprehensive build and test validation on Java 25. Fix any remaining test failures until 100% pass rate achieved.

**Changes to Make**:
- [ ] Run full clean build: `mvn clean compile test-compile`
- [ ] Run full test suite: `mvn clean test`
- [ ] If test failures occur, analyze root cause and apply fixes immediately
- [ ] Re-run tests iteratively until 100% pass rate achieved or document unfixable limitations
- [ ] Verify no deprecation warnings or significant style issues

**Verification**:
- Command: `mvn clean test` (full build and test)
- JDK: Java 25
- Expected Result: 
  - ✅ Main source code compiles successfully
  - ✅ All test code compiles successfully
  - ✅ **100% test pass rate** (or document pre-existing baseline failures if applicable)
  - ✅ No runtime exceptions or deprecation warnings

**Acceptance Criteria**:
- All Upgrade Success Criteria met:
  - Compilation succeeds for main code and test code
  - Test pass rate ≥ baseline (or 100% if baseline was 100%)
  - All code targets Java 25 release version
  - All dependencies compatible with Java 25

## Key Challenges

1. **JavaFX Version Alignment**
   - **Challenge**: JavaFX must be version-aligned with the Java runtime (Java 25 requires JavaFX 25.0.x)
   - **Strategy**: Update `javafx.version` property in pom.xml to match Java 25. No API changes expected between releases.

2. **Module System Compatibility**
   - **Challenge**: JavaFX uses JPMS modules; Java 25 may have refined module policies
   - **Strategy**: Maven and JavaFX plugin handle module configuration automatically. No manual module-info.java changes needed.

3. **Build Tool Chain Verification**
   - **Challenge**: Ensuring Maven 3.9.14 (newly installed) correctly invokes Java 25 compiler
   - **Strategy**: Explicitly set JAVA_HOME to Java 25 during build. Test with `mvn -v` before compilation.

No major breaking changes expected for this LTS-to-LTS upgrade (Java 21 → 25).

## Plan Review

**Completeness Check**:
- ✅ Feasible upgrade path: Java 21 LTS → 25 LTS (both stable, well-tested)
- ✅ All dependencies analyzed for compatibility
- ✅ Environment tools (JDK 25, Maven 3.9.14) verified and installed
- ✅ Build strategy defined (5 steps: Setup, Baseline, Compiler, Framework, Validation)
- ✅ Test strategy defined (baseline comparison + 100% pass requirement)
- ✅ No unfixable blocking issues identified

**Known Limitations**: None identified; this is a straightforward LTS-to-LTS upgrade with minimal risk.

**Plan Status**: ✅ **READY FOR CONFIRMATION**
