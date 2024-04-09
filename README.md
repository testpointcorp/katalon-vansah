# Katalon Integration with Vansah Test Management for Jira

This tutorial guides you through the process of integrating Katalon WebUI tests with Vansah Test Management for Jira. Integrating Katalon with Vansah will allow you to send Test Case results from Katalon to your Jira workspace.

By following this setup, you can streamline your testing workflow, ensuring that test outcomes are recorded directly in your Jira workspace.

## Prerequisites
- **Katalon** WebUI test project is already setup.
- Make sure that [`Vansah`](https://marketplace.atlassian.com/apps/1224250/vansah-test-management-for-jira?tab=overview&hosting=cloud) is installed in your Jira workspace
- You need to Generate **Vansah** [`connect`](https://docs.vansah.com/docs-base/generate-a-vansah-api-token-from-jira-cloud/) token to authenticate with Vansah APIs.
## Configuration
**Setting Environment Variables** - Store your Vansah API token as an environment variable for security. 

For Windows (use cmd)
```cmd
setx VANSAH_TOKEN "your_vansah_api_token_here"

```
For macOS
```bash
echo export VANSAH_TOKEN="your_vansah_api_token_here" >> ~/.bash_profile

source ~/.bash_profile

```
For Linux (Ubuntu, Debian, etc.)
```bash
echo export VANSAH_TOKEN="your_vansah_api_token_here" >> ~/.bashrc

source ~/.bashrc

``` 
## Implementation

To enable Vansah integration in any WebUI Katalon project, follow these steps:

1. **Place the VansahBinding.java File**: Ensure that [`VansahBinding.java`](/Include/scripts/groovy/VansahBinding.java) is located in the `Include/scripts/groovy` directory of your project.

2. **Add the Vansah Test Listener**: Add or Create **@AfterTestCase** listener so that after running each Test Case we can send the test results of the same test Case to Vansah.

   Ex: [`VansahListeners.groovy`](Test%20Listeners/VansahListeners.groovy)
   ```groovy
      import com.kms.katalon.core.annotation.AfterTestCase
      import com.kms.katalon.core.context.TestCaseContext

      import VansahBinding;

      class VansahListeners {
        /**
         * Executes after every test case ends to send results to Vansah.
         * @param testCaseContext Context of the executed test case.
         */
        @AfterTestCase
        def AfterTestCase(TestCaseContext testCaseContext) {
        
            // Retrieve Test Case and Asset details
            def vansahData = testCaseContext.getTestCaseVariables()
            def testCaseKey = vansahData.get("TestCaseKey")
            def assetKey = vansahData.get("Asset")
        
            VansahBinding vb = new VansahBinding();
           vb.sendResultstoVansah(testCaseKey, assetKey, testCaseContext.getTestCaseStatus());
        }
      }

		
    	}
      }
   ```

4. **Configure Test Run Properties**: Modify `Profiles/default.glbl` with your specific Vansah URL and test run properties to ensure proper configuration and communication with Vansah.
    ![VansahVariablesinDefaultProfile](/Asset/default_profile.png)
    
    OR
    
    Copy and Paste below script to your default profile, include inside `<GlobalVariableEntities>`
    
    ```xml
    <GlobalVariableEntity>
      <description>Required : Obtain your Vansah Connect URL from Vansah Settings > Vansah API Tokens </description>
      <initValue>'https://prod.vansahnode.app'</initValue>
      <name>Vansah_URL</name>
   </GlobalVariableEntity>
   <GlobalVariableEntity>
      <description>Optional : Provide your Sprint Name</description>
      <initValue>'SM Sprint 1'</initValue>
      <name>SprintName</name>
   </GlobalVariableEntity>
   <GlobalVariableEntity>
      <description>Optional : Provide your Release Name</description>
      <initValue>'Release 24'</initValue>
      <name>ReleaseName</name>
   </GlobalVariableEntity>
   <GlobalVariableEntity>
      <description>Optional : Provide your Environment Name</description>
      <initValue>'UAT'</initValue>
      <name>EnvironmentName</name>
   </GlobalVariableEntity>
    ```

5. **Prepare Test Cases**: Incorporate test case and asset details directly within your Katalon test case files. This step ensures that all necessary information for Vansah reporting is readily available.

    ![VansahTestCaseandAssetDetails](/Asset/testCaseandAssetdetails.png)

## Conclusion

By following the above steps, your Katalon project will be equipped to send test run results directly to Vansah, streamlining your testing and reporting process. 

Ensure that all files are placed and configured as described to facilitate successful integration.

For more details on Katalon, visit the [Test Fixtures and Test Listeners in Katalon Studio](https://docs.katalon.com/katalon-studio/create-test-cases/test-fixtures-and-test-listeners-test-hooks-in-katalon-studio). 

For Vansah specific configurations and API details, please refer to the [Vansah API documentation](https://apidoc.vansah.com/).
