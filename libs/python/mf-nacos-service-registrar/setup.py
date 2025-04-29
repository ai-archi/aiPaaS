from setuptools import setup, find_packages

setup(
    name="mf-nacos-service-registrar",
    version="0.1.1",
    packages=find_packages(where="src"),
    package_dir={"": "src"},
    install_requires=[
        "nacos-sdk-python>=2.0.0",
        "netifaces>=0.11.0",
    ],
    author="Jin",
    author_email="jin@example.com",
    description="A Nacos service registrar with enhanced IP detection",
    long_description="A Nacos service registrar that uses netifaces to detect the first non-loopback network interface IP",
    long_description_content_type="text/markdown",
    url="https://github.com/yourusername/mf-nacos-service-registrar",
    classifiers=[
        "Programming Language :: Python :: 3",
        "License :: OSI Approved :: MIT License",
        "Operating System :: OS Independent",
    ],
    python_requires=">=3.7",
) 