# CI/CD Workflow for Sushil POC Trade Store

This directory contains the GitHub Actions workflow for continuous integration and continuous delivery (CI/CD) of the Sushil POC Trade Store project.

## How It Works
- **Trigger:** The workflow runs automatically on every push or pull request to the `main` branch.
- **Steps:**
  1. **Checkout code:** Retrieves the latest code from the repository.
  2. **Set up JDK 17:** Prepares the environment for building Java 17 projects.
  3. **Cache Maven dependencies:** Speeds up builds by caching dependencies.
  4. **Build and Test:** Runs `mvn clean install` and `mvn test` to build and test the project.
  5. **Security Scan:** Runs OWASP Dependency-Check to scan for open source vulnerabilities.
  6. **Upload Report:** Uploads the security scan report as a build artifact.
  7. **Fail on Critical Vulnerabilities:** Fails the build if critical or blocker vulnerabilities are found in dependencies.


## How to Update & Extend
- **Add More Steps:** To deploy, notify, or run additional checks, add new steps to `.github/workflows/ci-cd.yml`.
- **Change Triggers for Multiple Branches:**
  - Edit the `on:` section to include more branches (e.g., `develop`, `release/*`, `feature/*`, `bugfix/*`).
  - Example:
    ```yaml
    on:
      push:
        branches: [ main, develop, 'release/*', 'feature/*', 'bugfix/*' ]
      pull_request:
        branches: [ main, develop, 'release/*' ]
    ```
- **Tagging for Release:**
  - Add a workflow trigger for tags to automate release builds and deployments.
  - Example:
    ```yaml
    on:
      push:
        tags:
          - 'v*.*.*'
    ```
  - Use `git tag v1.0.0 && git push origin v1.0.0` to create and push a release tag.
- **Switch to Jenkins:** If your organization uses Jenkins, create a `Jenkinsfile` with similar build, test, and scan steps.

## Recommended Branching Strategy
- **main**: Always production-ready. Only stable, tested code is merged here.
- **develop**: Integration branch for features and bugfixes. Regularly merged into main after successful testing.
- **release/*:** Used to prepare a new production release. Allows for final testing and hotfixes before merging to main.
- **feature/*:** Short-lived branches for new features. Merged into develop when complete.
- **bugfix/*:** Short-lived branches for bug fixes. Merged into develop or release as appropriate.
- **hotfix/*:** For urgent fixes to production. Branched from main, merged back to main and develop.

### Multiple Releases in Parallel
- Multiple `release/*` branches can exist for parallel release trains (e.g., v1.0, v2.0).
- Each release branch is independently tested and patched until merged to main.
- Tag releases (e.g., `v1.0.0`, `v2.0.0`) for traceability and automated deployment.

This strategy supports safe, auditable, and scalable enterprise release management.

## Why This Workflow
- **Automates Quality:** Ensures every code change is built, tested, and scanned for vulnerabilities before merging.
- **Modern DevOps:** Uses GitHub Actions for easy integration with GitHub-hosted code and PRs.
- **Banking-Grade Security:** Fails the build on critical vulnerabilities, supporting compliance and risk management.

For more details, see the main project `README.md`.
