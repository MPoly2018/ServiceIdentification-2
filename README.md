# ServiceIdentification-2

**ServiceMiner** is a static-analysis-based service identification tool that supports the migration of legacy Java-based enterprise systems toward a **Service-Oriented Architecture (SOA)**. It automatically discovers and categorizes service candidates according to their types such as **Entity**, **Utility**, and **Application services**—to aid architects and developers during the early stages of system migration.

---


# Repo Structure
ServiceIdentification-2/

├─ GroundTruth and Results/ –> GroundTruth identification and service identification results 

├─ Interviews Transcripts/  -> Contains transcripts of our interviews sessions

├─ MOGA-WSI master          -> Contains the source code of a SOTA service identification approach

├─ ServiceMiner master      -> Contains the source code our service identification approach



---

## 🛠 Installation

Ensure you have the following prerequisites installed:

- **Java JDK** (version 11 or later)
- **Apache Maven** (version 3.6+)

### Clone and Build the Project

```bash
git clone https://github.com/MPoly2018/ServiceIdentification-2.git
cd ServiceIdentification-2
mvn clean install
 
