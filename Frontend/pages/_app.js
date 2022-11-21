import "../styles/globals.css";

import { ChakraProvider } from "@chakra-ui/react";
import Head from "next/head";
import { useRouter } from "next/router";
import React from "react";
import { Provider } from "react-redux";

import Footer from "../components/Footer";
import NavBar from "../components/NavBar";
import ToastController from "../components/ToastController";
import configureStore from "../stores/store";
import theme from "../theme";

const SiteHead = ({ title }) => (
  <Head>
    <title>{title}</title>
    <meta name="title" content="Zoomer | Ridesharing made easy" />
    <meta
      name="description"
      content="Zoomer is a ride sharing service started by UTM alumni that uses a lot of tech buzzwords!"
    />
    <link rel="icon" href="/favicon.ico" />
    <link rel="apple-touch-icon" href="/logo192.png" />
    <meta property="og:type" content="website" />
    <meta property="og:url" content="https://www.zoomer.com/" />
    <meta property="og:title" content="Zoomer | Ridesharing made easy" />
    <meta
      property="og:description"
      content="Zoomer is a ride sharing service started by UTM alumni that uses a lot of tech buzzwords!"
    />
    <meta property="og:image" content="/logo512.png" />
    <meta property="twitter:card" content="summary_large_image" />
    <meta property="twitter:url" content="https://www.zoomer.com/" />
    <meta property="twitter:title" content="Zoomer | Ridesharing made easy" />
    <meta
      property="twitter:description"
      content="Zoomer is a ride sharing service started by UTM alumni that uses a lot of tech buzzwords!"
    />
    <meta property="twitter:image" content="/logo512.png" />
  </Head>
);

const PageWrapper = ({ children, title }) => (
  <div className="container">
    <SiteHead title={title} />
    <NavBar />
    <main className="main">{children}</main>
    <Footer />
  </div>
);

const App = ({ Component, pageProps }) => {
  const { pathname } = useRouter();
  const store = configureStore(pageProps.initialReduxState);
  const pathToTitle = {
    "/": "Zoomer | Ridesharing made easy",
    "/ride": "Zoomer | Call a ride",
    "/trips": "Zoomer | Trip History",
    "/profile": "Zoomer | Your Profile",
  };
  return (
    <ChakraProvider theme={theme}>
      <Provider store={store}>
        <ToastController />
        <PageWrapper title={pathToTitle[pathname]}>
          <Component {...pageProps} />
        </PageWrapper>
      </Provider>
    </ChakraProvider>
  );
}

export default App;
