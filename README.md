# Budget Management Application
The Budget Management Application is an Android app developed in Kotlin that helps users manage their finances efficiently. It offers features for recording and categorizing transactions, visualizing income and expenses with interactive charts, and tracking recurring payments. Users can export data to Excel and back it up securely to Firebase Storage. The app also includes Firebase authentication for data security and provides a simple, intuitive interface for easy financial management.


<img src="https://github.com/user-attachments/assets/7bf563a2-c4b3-4aac-a2a6-f7b1689c59db" width="1000" height="400">




## Technologies Used
### Kotlin
The primary programming language used for developing the Budget Management Application.

### SQLite
Used for local data storage, enabling efficient and offline access to financial records.

<img src="https://github.com/user-attachments/assets/4ab99986-3ab0-496a-909c-c3df112184c5" width="900" height="720">

### Firebase Authentication
Provides user authentication functionality, allowing users to register and log into the app securely.

<img src="https://github.com/user-attachments/assets/4ee19339-8f0d-4fa9-883b-f89747a4cd6e" width="200" height="400">
<img src="https://github.com/user-attachments/assets/f970b5c4-15f7-4943-9c22-67c74b64f6ce" width="200" height="400">

### Firebase Storage
Stores the backup of financial data, ensuring data security and accessibility across devices.

### MPAndroidChart
MPAndroidChart is an Android library that offers various chart types, such as line, bar, and pie charts, along with features like scaling, scrolling, and animations. In the application, a pie chart is used for monthly data, while a bar chart visualizes weekly data. This library allows users to easily interpret their data through visualizations, making it simpler to understand compared to viewing raw numbers.

<img src="https://github.com/user-attachments/assets/23a17931-d9f1-411d-8911-40aa62044e4c" width="200" height="400">
<img src="https://github.com/user-attachments/assets/2d5d503b-a6b4-4e2b-9ecf-133bbc2e7215" width="200" height="400">

### Apache Commons Math
The Apache Commons Math library is used to perform complex mathematical operations, such as creating a linear regression model to estimate user incomes and the time required to reach financial goals. The model uses daily and regular income data, with daily records representing the independent variable (date) and income amounts allocated to financial targets as the dependent variable. Regular income is averaged and included to provide broader estimates. This model predicts future income and estimates the time needed to achieve financial goals, helping users plan their financial actions effectively.

<img src="https://github.com/user-attachments/assets/ab0a8a51-5a26-4507-8956-dcb5c43902d0" width="400" height="400">

### Retrofit
Retrofit and Gson libraries are used to provide users with current exchange rate data. Retrofit manages HTTP requests to an external API, retrieving exchange rates in JSON format, while Gson converts this data into Kotlin objects. This allows users to save income and expense data in various currencies, using up-to-date exchange rates, making it convenient during holidays, business trips, or relocations. Additionally, when users change the default currency in the app settings, all related data, such as financial goals and budget limits, are automatically updated to reflect the correct exchange rates.

<img src="https://github.com/user-attachments/assets/dd8dcb70-e71d-4212-9a47-bdcb69c81e99" width="200" height="400">

### RXJava
Implemented for reactive programming, enabling efficient management of asynchronous data streams.

### Glide
An efficient image loading and caching library for Android, used to load and display images where needed in the app.

### Splash Screen
A splash screen was added to enhance the app's launch experience.

<img src="https://github.com/user-attachments/assets/ad167ce7-3efe-48f0-b9b2-30d6eac2b82b" width="200" height="400">

### Onboarding Screens
Onboarding screens provide users with essential information and guide them through the app's key features during the initial setup.

<img src="https://github.com/user-attachments/assets/f9ab3a81-8a23-456b-b45b-48e2537ddee6" width="200" height="400">
<img src="https://github.com/user-attachments/assets/f467fa6b-221a-44a8-80f5-6709a1bc93af" width="200" height="400">

### Apache POI
The application uses Apache POI to export income and expense data as Excel files, catering to users who manage their budget via Excel and want to continue budget management on their mobile phones when away from their computer. Users can select a specific month and choose to export only income data, only expense data, or both in Excel format, making it convenient for seamless budget tracking across devices.

<img src="https://github.com/user-attachments/assets/ebac2925-3a90-4c6b-b16b-8557951f13f7" width="200" height="400">



###  Other Screenshots

<img src="https://github.com/user-attachments/assets/d1d08767-106e-4c62-8bbe-16efdea4d02c" width="200" height="400">
<img src="https://github.com/user-attachments/assets/b2d03c78-ced5-4a2f-9f2f-2224ec1d71f1" width="200" height="400">
<img src="https://github.com/user-attachments/assets/4df4a5e0-f244-4c08-ab93-81fd3f427bec" width="200" height="400">
<img src="https://github.com/user-attachments/assets/4e30e1ba-f67d-4b0a-89e8-0b1e3534c056" width="200" height="400">
<img src="https://github.com/user-attachments/assets/55da8778-79bb-42f8-991c-0c63e929edb8" width="200" height="400">
<img src="https://github.com/user-attachments/assets/52e8bf38-a9ab-4505-9d00-78564a73d471" width="200" height="400">
<img src="https://github.com/user-attachments/assets/9fb5dc15-f0bf-4fa6-8242-8dad031b315c" width="200" height="400">




